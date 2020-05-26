package net.listcode.commons;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TestLock {

    private static ConcurrentHashMap<String, Integer> simpleLock = new ConcurrentHashMap<>();
    private static synchronized boolean tryLock(String key) {
        int id = Thread.currentThread().hashCode();
        simpleLock.putIfAbsent(key, id);
        return simpleLock.getOrDefault(key, id - 1) == id;
    }

    private static synchronized void releaseLock(String key) {
        int id = Thread.currentThread().hashCode();
        if (simpleLock.getOrDefault(key, id -1) == id) {
            simpleLock.remove(key);
        }
    }

    private static void logLockState() {
        log.debug("size={}, data={}", simpleLock.size(), simpleLock);
    }

    public static void main(String[] args) throws Exception{

        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 20; j++) {
                            String tid = Thread.currentThread().getName();
                            String key = "" + j /10;
                            log.info("[{}] ++++++++TRY LOCK-{}", tid, key);
                            logLockState();
                            boolean hasLock = tryLock(key);
                            logLockState();
                            if (hasLock) {
                                log.info("[{}] ======GOT LOCK-{}, SLEEP 2 seconds", tid, key);
                                Thread.sleep(2000);
                                log.info("[{}] ########WILL RELEASE LOCK-{}", tid, key);
                                logLockState();
                                releaseLock(key);
                                logLockState();
                            } else {
                                Thread.sleep(50);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }

        for (Thread t : list) {
            t.start();
        }
        logLockState();
    }
}
