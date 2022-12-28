//package com.kekwy.jw.server.util;
//
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public class ThreadSafeList<T> {
//
//	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//	private final List<T> list = new LinkedList<>();
//	private Iterator<T> iterator;
//
//	private final ExecutorService service = Executors.newCachedThreadPool();
//
//	public void toRead() {
//		lock.readLock().lock();
//	}
//
//	public void finishRead() {
//		lock.readLock().unlock();
//	}
//
//	public void add(T t) {
//		service.execute(()->{
//			lock.writeLock().lock();
//			list.add(t);
//			lock.writeLock().unlock();
//		});
//	}
//}
