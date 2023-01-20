# j05：自制坦克大战#part1

> 201220214 张宇轩
>
> 1.  Java 高级程序设计课程大作业的第一部分；
> 2. 基于 Javafx 手搓的一个坦克大战小游戏，比较简陋；
> 3. 每个活动实体是一个线程，主要用于加强对线程的理解以及练习 Java 中与线程相关的操作；
> 4. 更多细节详见大作业完整报告。

* **[展示视频](#展示视频)**


## 展示视频

[![image-20230120234016264](https://assets.kekwy.com/images/image-20230120234016264.png)](https://www.bilibili.com/video/BV1VP4y1r7pL/)

## 多线程设计

[class]GameScene -> [func] addGameObject：

```java
// 将游戏对象加入对应的渲染图层
layers[gameObject.getLayer()].add(gameObject);
if (gameObject instanceof Runnable runnable) {
	service.execute(runnable);
}
```

新游戏对象加入场景时，判断其是否实现 runnable 接口，若其实现 runnable 接口则将其加入当前场景的线程池中，线程池自动为其创建线程并进行管理。

### 坦克行为

[class]Tank：

```java
@SuppressWarnings("BusyWait")
@Override
public void run() {
	while (this.isActive()) {
		move();				// 移动
		try {
			doCollide();	// 碰撞检测
			check(System.currentTimeMillis()); // 检查对象状态
			Thread.sleep(UPDATE_INTERVAL);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
```

每一个坦克的线程中周期性地进行上述操作，直到调用 GameObject 中的方法 isActive 返回 false（表示游戏对象将要被销毁）。

- 移动：

  [class]Tank：

  ```java
  private void move() {
     // 若当前处于移动状态，进行移动
     if (state == State.STATE_MOVE) {
        double x = this.transform.getX();
        double y = this.transform.getY();
        // 根据方向和速度改变坐标
        switch (direction) {
           case DIR_UP -> y -= speed;
           case DIR_DOWN -> y += speed;
           case DIR_LEFT -> x -= speed;
           case DIR_RIGHT -> x += speed;
        }
        // 调用场景中更新游戏对象的方法
        this.getParent().update(this, x, y, TANK_RADIUS);
     }
  }
  ```

- 碰撞：

  ```java
  // 当与坦克相碰时：
  if (gameObject instanceof Tank tank) {
     if (tank.group == this.group) {
        // TODO 队友 BUFF
     } else {
        // 不同阵营的坦克相碰时，则同归于尽
        ...
     }
  } else if (gameObject instanceof Bullet bullet && bullet.getFrom().group != this.group) {
     // 当碰撞对象为子弹且开火的坦克与自己不是同一阵营时，
     // 根据子弹攻击力扣除当前坦克对应的血量，
     // 并将该子弹的 active 设置为 false，
     // 表示即将销毁该子弹。
     ...
  } else if (gameObject instanceof MapTile mapTile) {
     // 若碰撞对象为地图块且为树叶时，
     if (mapTile.getType() == MapTile.Type.TYPE_COVER) {
        // 隐藏坦克名字的血条
        setVisible(false);
        // 将坦克的覆盖状态设置为 true，
        // 表示当前坦克被树叶覆盖。
        isCovered = true;
     } else {
        // 若为其他类型的地图块，则坦克不能穿过，
        // 需要根据两者的相对位置重置坦克的坐标。
        ...
     }
  }
  ```

实现细节详见大作业报告。

#### 敌人坦克

> 暂时基于随机算法的行为设计。

敌人坦克需要由程序自驱动，除了上述操作外还需要检测自身状态，并自动执行某些动作。

[class]EnemyTank：

- 状态转换：

  ```java
  @Override
  public void check(long timestamp) {
  	...
      // 若当前时间与上次状态改变时间差值大于状态改变间隔
      // （状态改变间隔同样由每次进行状态更新时随机生成），
      // 进行敌人坦克的状态改变。
  	if (timestamp - lastChangTime > changeInterval) {
  		// 随机生成下一次的状态改变间隔
  		changeInterval = (int)TankWarUtil.getRandomNumber(1000, 2000);
          // 随机设置一个状态：闲置或移动
  		setState(State.values()[(int)TankWarUtil.getRandomNumber(0, 2)]);
  		// 随机设置一个方向：上、下、左、右
          setDirection(Direction.values()[(int)TankWarUtil.getRandomNumber(0, 4)]);
  		// 更新上次状态改变时间
          lastChangTime = timestamp;
  	}
      // 开火概率为 0.05，并且需要大于开火间隔
  	if (Math.random() < 0.05 && timestamp - fireTime > FIRE_INTERVAL) {
          // 更新上次开火时间
  		fireTime = timestamp;
  		fire();
  	}
  }
  ```

### 子弹行为

子弹移动和碰撞行为的实现方式与坦克的实现相同，此处不重复说明。

### 线程安全

#### 更新坐标

为了最大限度的减少碰撞检测时遍历的对象并且该项目中出现的游戏对象大小相近，故在游戏场景中将整个场景按块划分，每个游戏对象在加入场景的同时按坐标计算出其所属的区块并将其加入，而在其坐标改变时，若其移动到了新的区块，则将其加入新的区块并从之前的区块中移除。这样，在碰撞检测的时候就只需要读取对象所在的区块及其一周的区块（一共九个区块）中的对象，对齐进行进一步判断即可，而不必遍历所有在场的游戏对象。

加入区块和离开区块的操作不影响当前对象主线程的执行，其需要进行同步操作，故通过线程池创建“后台”线程实现上述操作，避免对象主线程阻塞。

**一个假设：**通过后台线程“延时”加入区块可能会导致判断碰撞时读取到旧的数据，造成碰撞判断出错——本该相碰的两个物体，由于其中一个物体还没有加入其真实所处的区块，导致碰撞检测时从该区块中没有读出该对象。

下面将从两个方面论述上述假设的结果不会对游戏运行产生影响：

- 首先，碰撞检测每个周期都会进行，而每个周期内游戏对象最多移动一次，一般情况下游戏对象的移动不会太快，即经过一个周期游戏对象的位置可能变化不大，而 enter、leave 操作从加入线程池到被调度执行的时间远小于游戏对象主线程的循环周期，故这次循环没有检测到的碰撞在下个周期内仍然可以被检测到而不产生过大的误差。

- 更进一步，游戏对象是先加入新区块，再退出原来的区块，故不会产生一个对象从所有的区块中都无法访问的情况。又由于设置区块的大小大于游戏对象的大小，两个物体相撞时，若一个物体正在跨区块，那么其之前所处的区块一定在碰撞检测的对象所读取的九个区块中（如下图，无论是站在谁的角度，对方之前所在的区块都在自己的扫描范围内）。故碰撞检测依然可以正确执行，故假设本身就不成立。

  <img src="https://assets.kekwy.com/images/image-20230120231019960.png" alt="image-20230120231019960" style="zoom: 67%;" />

[class]GameScene：

```java
public void update(GameObject object, double x, double y, int offset) {
   int oldRow = object.transform.getGridRow();
   int oldCol = object.transform.getGridCol();
   int row = (int) y / GRID_SIZE;
   int col = (int) x / GRID_SIZE;
   if (row != oldRow || col != oldCol) {
      object.transform.setGridRow(row);
      object.transform.setGridCol(col);
      synchronized (service) {
         service.execute(() -> {
		    grid[row][col].enter(object);
		    grid[oldRow][oldCol].leave(object);
         });
      }
   }
}
```

对于每个网格对象的获取对象列表操作和 enter、leave 操作使用读写锁（java.util.concurrent.locks.ReentrantReadWriteLock）同步：

[class]GameScene -> [class]GridCell：

```java
private final ReentrantReadWriteLock readWriteLock 
    = new ReentrantReadWriteLock();

public void enter(GameObject object) {
   readWriteLock.writeLock().lock();
   ...
   readWriteLock.writeLock().unlock();
}

public void leave(GameObject object) {
   readWriteLock.writeLock().lock();
   ...
   readWriteLock.writeLock().unlock();
}

public void getObjects(List<GameObject> list) {
   readWriteLock.readLock().lock();
   ...
   readWriteLock.readLock().unlock();
}
```

特别地，由于所有的 enter、leave 操作都是通过线程池中的线程执行的，而各线程之间的执行进度与调度时机不确定，试想一种情况：

某个对象先进入某个区块，片刻后又离开，假如该在执行离开该区块的 leave 操作时，相应的 enter 操作还没有执行（要从对应的区块中移除该对象，但是其还没有加入）。无论是直接执行移除操作（移除不存在的对象），还是什么都不做（后续执行的 enter 操作并不知道其自身的逻辑顺序，仍会将该对象重新加入该区块），都会产生很严重的错误。

以下将讨论项目中采用的解决方案：

[class]GameScene -> [class]GridCell：

```java
// “未来”要被移除的游戏对象列表
private final List<GameObject> removeBuffer = new ArrayList<>();

public void enter(GameObject object) {
   readWriteLock.writeLock().lock();
   // 若该对象在该区块的待删除列表中，
   // 则将其从待删除列表中移除，而不加入区块的对象列表。
   if (removeBuffer.contains(object)) {
      removeBuffer.remove(object);
      readWriteLock.writeLock().unlock();
      return;
   }
   objects.add(object);
   readWriteLock.writeLock().unlock();
}

public void leave(GameObject object) {
   readWriteLock.writeLock().lock();
   // 对象列表中不包含目标对象，
   // 则将其加入待移除列表中。
   if (!objects.remove(object)) {
      removeBuffer.add(object);
   }
   readWriteLock.writeLock().unlock();
}
```

由于在游戏进行时，leave 操作与 enter 操作一定是成对出现的，一条 leave 操作一定对应一个 enter 操作，而当执行 leave 操作时发现区块中没有该对象，则说明将由一个延迟抵达的 enter 操作，故先将该对象加入一个列表，当 enter 操作执行时，告知它“这个对象未来要被删掉了，你就不要加进来啦”。

#### 碰撞处理

> 包含典型的哲学家问题

由于需要保证同一次碰撞只生效一次，并且正在参与碰撞处理的对象不能参与其他的碰撞处理（如 A 与 B 碰，B 与 C 碰，假设先判断处理A与B的碰撞，由于在检测到B与C的碰撞时，B正在参与碰撞处理，则B与C的碰撞必须要等待 A 与 B 的碰撞处理结束后才能进行处理），故在碰撞处理时需要进行线程同步，且需要获取两个对象的锁。

而同时获取两个锁就是典型的[哲学家就餐问题](https://blog.csdn.net/theLostLamb/article/details/80741319)，不能使用一般的方法依次获取两个锁。

这次通过 `trylock()` 方法实现：

> trylock()：成功获取锁时返回 true，锁被占用时直接返回 false 而不阻塞。

```java
while (true) {
   // 若自身的锁被占用，则直接阻塞等待
   if (!this.collideLock().tryLock()) {
      this.collideLock().lock();
   }
   // 在拿到自己的锁后，如另一个对象的锁获取失败
   if (!gameObject.collideLock().tryLock()) {
      // 将自己的锁释放
      this.collideLock().unlock();
      // 阻塞等待在另一个对象的锁上
      gameObject.collideLock().lock();
      // 从阻塞等待中被唤醒时，释放持有的锁
      gameObject.collideLock().unlock();
      // 再进行判断
      continue;
   }
   // 直到同时获得两个对象的锁，退出循环
   break;
}
// 若两个对象任意一个处于待销毁的状态，
// 则直接释放所有锁，跳过两者的碰撞处理。
if (!this.isActive() || !gameObject.isActive()) {
   gameObject.collideLock().unlock();
   this.collideLock().unlock();
   if (!this.isActive()) {
      return;
   } else {
      continue;
   }
}
```

#### 刷新屏幕

对于屏幕刷新（刷帧）线程频繁读取游戏对象的坐标等数据的操作不需要做线程同步，因为就算读取数据的同时数据被修改，导致读到了旧数据，使当前帧的显示出现错误，下一帧刷新时就会重新读到更新后的数据，正确显示对象状态。综上，不同步的代价仅仅是可能导致一帧显示错误，而这个错误人眼往往无法分辨，故可以不进行线程同步，从而避免频繁同步而带来的不必要的开销。



【感谢评阅】
