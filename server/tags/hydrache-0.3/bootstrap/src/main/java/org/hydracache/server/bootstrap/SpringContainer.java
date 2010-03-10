/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hydracache.server.bootstrap;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContainer implements Container {

    private ClassPathXmlApplicationContext applicationContext;

    private boolean running;

    public SpringContainer(Runtime runtime) {
        this(runtime, new String[] { "http-server-context.xml" });
    }

    SpringContainer(Runtime runtime, String[] configurationLocations) {
        applicationContext = new ClassPathXmlApplicationContext(
                configurationLocations);
        
        runtime.addShutdownHook(new ShutdownThread());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.bootstrap.Container#shutdown()
     */
    @Override
    public void stop() {
        running = false;
        
        applicationContext.stop();
        
        applicationContext.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.bootstrap.Container#startup()
     */
    @Override
    public void start() {
        applicationContext.start();

        running = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.bootstrap.Container#isStarted()
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    private class ShutdownThread extends Thread {

        public ShutdownThread() {
            super(ShutdownThread.class.getSimpleName());
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            SpringContainer.this.stop();
        }

    }

}
