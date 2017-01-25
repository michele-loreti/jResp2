package org.cmg.jresp.knowledge2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Implementa TupleSpaceLock in modo da avere un accesso esclusivo per il read e l'in. 
 * Permette threads multipli per l'out.
 */
public class MOutSReadSIn implements TupleSpaceLock {
	private ReadWriteLock lock;
	private Lock r;
	private Lock w;

	public MOutSReadSIn() {
		lock = new ReentrantReadWriteLock();
		r = lock.readLock();
		w = lock.writeLock();
	}

	public void readLock() {
		w.lock();
	}

	public void inLock() {
		w.lock();
	}

	public void outLock() {
		r.lock();
	}

	public void readUnlock() {
		w.unlock();
	}

	public void inUnlock() {
		w.unlock();
	}

	public void outUnlock() {
		r.unlock();
	}

}
