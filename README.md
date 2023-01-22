# j06：自制坦克大战#part2

> 1. Java 高级程序设计课程大作业的第二部分；
> 2. 学习 JavaIO 的相关知识，学习如何通过 maven 进行项目管理；
> 3. 基于 Java 中的对象序列化，通过将对象写入文件/从文件读取实现游戏进度的保存/恢复；
> 4. 通过 maven 添加依赖，并使用外部依赖 poi 工具读取 Excel 文件，根据 Excel 文件内容加载地图；
> 5. 针对地图加载部分的代码编写单元测试用例。

* **[展示视频](#展示视频)**
* **[进度保存](#进度保存)**
  * [上层调用](#上层调用)
  * [底层实现](#底层实现)
* **[地图加载](#地图加载)**
  * [效果展示](#效果展示)
  * [代码实现](#代码实现)
* **[单元测试](#单元测试)**
  * [测试用例](#测试用例)
  * [测试结果](#测试结果)

## 展示视频

[![image-20230122114028308](https://assets.kekwy.com/images/image-20230122114028308.png)](https://www.bilibili.com/video/BV1wR4y1Y7VB/)

## 进度保存

#### 上层调用

保存：

[class]Main：

```java
@Override
public void stop() throws Exception {
   // 若当前 stage 上的场景是单人游戏场景，
   // 则在退出前将游戏进度写入磁盘。
   if (stage.getScene() instanceof LocalPlayScene scene) {
      scene.saveToDisk();
   }
   System.exit(-1);
}
```

重写 JavaFx 中 Application 类中的方法 `stop()`，该方法将在点击窗口关闭按钮时被调用。点击关闭游戏时，若当前有正在进行的游戏，则进行保存。

若保存成功会在游戏根目录下的 save/ 子目录下生成一个以“save”为前缀，“.tmp”为后缀的临时文件，其中保存了游戏对象序列化后的数据。

恢复：

[class]Main -> [func]start(Stage stage)：

```java
File file = new File("./save/");
if (file.exists() && file.isDirectory()
      && Objects.requireNonNull(file.listFiles()).length > 0) {
   // 如果存在则弹出会话窗
   scene = showAlert(Objects.requireNonNull(file.listFiles())[0]);
} else {
   // 如果不存在则正常加载主场景
   scene = new MainScene();
}
```

在游戏窗口初始化完毕后，判断游戏根目录下 save/ 子目录是否为空。不为空说明存在保存进度的临时文件，这里会弹出一个对话框，提示玩家。

![image-20230122115846356](https://assets.kekwy.com/images/image-20230122115846356.png)

```java
@SuppressWarnings("OptionalGetWithoutIsPresent")
private GameScene showAlert(File file) {
   // 生成选择对话框
   Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
   // 设置头文字
   alert.setHeaderText("上次关闭游戏时有一场未完成的单人游戏");
   // 设置对话框内容
   alert.setContentText(
         """
               选择“确定”恢复上次游戏进度，
               选择“取消”或关闭对话框跳转至主界面,
               并删除保存上次进度的临时文件，
               删除操作无法恢复。"""
   );
   // 展示对话框并阻塞直到玩家确认
   Optional<ButtonType> result = alert.showAndWait();

   GameScene scene;
   
   // 如果玩家点击“确定”按钮，则从磁盘加载游戏进度；
   // 否则正常加载主场景。
   if (result.get() == ButtonType.OK) {
      LocalPlayScene playScene = new LocalPlayScene();
      playScene.loadFromDisk(file);
      scene = playScene;
   } else {
      // ... user chose CANCEL or closed the dialog
      scene = new MainScene();
   }
   // 删除临时文件
   //noinspection ResultOfMethodCallIgnored
   file.delete();
   return scene;
}
```

#### 底层实现

保存：

[class]LocalPlayScene：

```java
public void saveToDisk() {
   // 结束当前场景中的所有对象线程
   this.stop();
   // 如果当前场景的状态为游戏结束则不进行进度保存
   if (over) {
      return;
   }
   try {
      // 创建临时文件
      File saveFile = File.createTempFile("save", ".tmp", new File("./save/"));
      // 创建对象输出流
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
      // 对象写入顺序：
      // currentLevel, 
      // isWin, 
      // playing, 
      // level中的EnemyCount变量, 
      // 场景中的游戏对象
      oos.writeObject(currentLevel);
      oos.writeObject(isWin);
      oos.writeObject(playing);
      oos.writeObject(getLevel().getEnemyCount());
      for (GameObject object : super.objectList) {
         // 跳过一些不需要保存的对象
         if (object instanceof BackGround ||
               object instanceof OverBackGround ||
               object instanceof PassNotice ||
               object instanceof Trigger) {
            continue;
         }
         // 由于在 stop() 方法中所有的对象的 active
         // 都被置为 false，则在保存前需要将其还原为 true。
         object.setActive(true);
         oos.writeObject(object);
      }
      // 写入 null 用于标志文件末尾
      oos.writeObject(null);
      oos.close();
   } catch (IOException e) {
      throw new RuntimeException(e);
   }
}
```

恢复：

[class]LocalPlayScene：

```java
public void loadFromDisk(File file) {
   ObjectInputStream ois;
   try {
      ois = new ObjectInputStream(new FileInputStream(file));
   } catch (IOException e) {
      System.out.println("找不到文件：" + file);
      throw new RuntimeException(e);
   }

   try {
      // 先按顺序读取特殊变量
      currentLevel = (int) ois.readObject();
      isWin = (boolean) ois.readObject();
      playing = (boolean) ois.readObject();
      getLevel().setEnemyCount((Integer) ois.readObject());

      GameObject object;

      int enemyCount = 0;

      // 读出文件中的所有游戏对象，按种类分别进行处理
      while ((object = (GameObject) ois.readObject()) != null) {
         if (object instanceof PlayerTank tank) {
            player = tank;
            // 由于 javafx 中的 Color 类不可序列化，
            // 则需要借助可序列化的 double 型的 rgb 对其进行复原。
            player.setColor(player.r, player.g, player.b);
            object.setParent(this);
            tank.recoveryFromDisk();
            continue;
         } else if (object instanceof MapTile tile) {
            mapTileList.add(tile);
         } else if (object instanceof EnemyTank enemy) {
            // 用于还原当前在场的敌人数量
            enemyCount++;
            enemy.setColor(enemy.r, enemy.g, enemy.b);
         } else if (object instanceof Bullet bullet) {
            bullet.setColor(bullet.r, bullet.g, bullet.b);
         }
         object.setParent(this);
         addGameObject(object);
      }

      EnemyTank.setCount(enemyCount);

   } catch (IOException e) {
      System.out.println("目标文件损坏（格式异常）");
      throw new RuntimeException(e);
   } catch (ClassNotFoundException e) {
      System.out.println("找不到指定的类");
      throw new RuntimeException(e);
   }

   try {
      ois.close();
   } catch (IOException e) {
      throw new RuntimeException(e);
   }

}
```

上述代码实现中的某些细节将在最终版的报告中进行优化。

## 地图加载

### 效果展示

本项目在游戏启动时基于 Excel 表格生成游戏地图：

![image-20230122122742595](https://assets.kekwy.com/images/image-20230122122742595.png)

程序识别的标准地图格式如下：

![image-20230122122835472](https://assets.kekwy.com/images/image-20230122122835472.png)

每个关卡的配置文件中都保存着该关卡对应地图文件的文件路径，同时关卡配置文件中还有许多其他可以自定义的参数：

```properties
# 请确保自定义关卡设置满足对应的格式
# 自定义关卡设置仅单人游戏下生效
# 关卡地图（必须为合法的.xlsx文件）
# 路径相对于游戏运行目录，非该设置文件目录
map_file=./maps/level1.xlsx
```

### 代码实现

添加依赖：

```xml
<dependencies>
    ...
    <!-- https://mvnrepository.com/artifact/org.apache.poi/poi -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
        <version>5.2.3</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.3</version>
    </dependency>
    ...
</dependencies>
```

读取Excel：

```java
/**
 * 读取 Excel 文件，并返回包含表格指定范围内容的二维数组
 * @param file Excel 文件输入流
 * @param rowFrom 读取的起始行
 * @param colFrom 读取的起始列
 * @param rowN 期望读取的行数
 * @param colN 期望读取的列数
 * @param sheetIndex 期望读取的工作簿索引号
 * @return 包含表格指定范围内容的二维数组
 */
public static int[][] readWorkBook(InputStream file, int rowFrom, int colFrom, int rowN, int colN, int sheetIndex) {
   // 根据读取大小设置保存结果的二维数组
   int[][] content = new int[rowN][colN];
   Workbook workbook = null;
   try {
      // 生成表格对象
      workbook = new XSSFWorkbook(file);//Excel 2007
   } catch (IOException e) {
      throw new RuntimeException(e);
   }
   // 获取指定工作簿
   Sheet sheet = workbook.getSheetAt(sheetIndex);
   for (int i = 0; i < rowN; i++) {
      // 根据行号获取工作簿中的指定行
      Row row = sheet.getRow(i + rowFrom);
      for (int j = 0; j < colN; j++) {
         // 根据列号获取指定的单元格
         Cell cell = row.getCell(j + colFrom);
         // 读取单元格数据并保存
         content[i][j] = Double.valueOf(cell.getNumericCellValue()).intValue();
      }
   }
   return content;
}
```

生成地图时遍历返回的二维数组，根据 Excel 文件中单元格的数据生成指定种类的地图块并计算其在场景中对应的坐标。

## 单元测试

> 由于项目代码略微冗杂，且其中涉及多线程并包含随机逻辑，短时间内无法对项目每个模块进行单元测试，此处仅针对本次实现的游戏进度保存方法进行单元测试。

对于涉及多线程的单元测试需要避免比较结果时存在活动的后台线程，导致对比结果的同时结果被后台线程修改，造成测试用例“假”不通过。

### 测试用例

```java
class LocalPlaySceneTest {

   @Test
   void saveAndLoad() {

      LocalPlayScene scene;
      try {
         new Thread(() -> Main.main(null)).start();
         Thread.sleep(1000);
         scene = new LocalPlayScene();
         scene.start();
         // 等待游戏运行十秒钟
         Thread.sleep(15000);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }

      // 获取游戏对象列表
      List<GameObject> objectList = scene.saveToDisk();
      File file = new File("./save/");

      assertTrue(file.exists());
      assertTrue(file.isDirectory());
      assertTrue(file.listFiles().length > 0);

      LocalPlayScene newScene = new LocalPlayScene();
      newScene.start();
      newScene.stop(); // 阻止读出的对象加入场景
      // 若对象读出后仍然直接加入场景，场景的线程池会直接尝试为该对象创建线程，
      // 造成对比结果的同时，后台线程修改了数据，导致测试用例“假”不通过。
      List<GameObject> newObjectList = newScene.loadFromDisk(file.listFiles()[0]);

      // 比较保存前和恢复后对象的数量。
      assertEquals(objectList.size(), newObjectList.size());

      for (int i = 0; i < newObjectList.size(); i++) {
         assertEquals(objectList.get(i), newObjectList.get(i));
      }
   }
}
```

### 测试结果

![image-20230122175033737](https://assets.kekwy.com/images/image-20230122175033737.png)

![image-20230122174746181](https://assets.kekwy.com/images/image-20230122174746181.png)

对于 Localscene 类的方法覆盖率为 55%，行覆盖率为 47%；对于我们针对测试的两个方法，实际的行覆盖率则达到了 80% 以上。



【感谢评阅】