package org.cmg.jresp.knowledge2;

public interface TupleSpaceLock {
	void readLock();

	void inLock();

	void outLock();

	void readUnlock();

	void inUnlock();

	void outUnlock();
}
