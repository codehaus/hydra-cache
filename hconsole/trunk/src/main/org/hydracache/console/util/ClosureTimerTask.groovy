package org.hydracache.console.util

/**
 * Created by nick.zhu
 */
class ClosureTimerTask extends TimerTask {

    def closure

    void run() {
        closure?.call()
    }

}
