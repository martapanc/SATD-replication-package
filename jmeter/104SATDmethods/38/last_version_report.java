public synchronized void clear() {
        failureCount = 0;
        successCount = 0;
        siteDown = false;
        successMsgSent = false;
        failureMsgSent = false;
        notifyChangeListeners();
    }
