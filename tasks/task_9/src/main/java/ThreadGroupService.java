public class ThreadGroupService {
    private final int INTERVAL = 3000;

    public void printTreadsInfo(ThreadGroup threadGroup){
        new Thread(() -> printInfoWithIntervals(threadGroup)).start();
    }

    private void printInfoWithIntervals(ThreadGroup threadGroup) {
        while(threadGroup.activeCount() > 0){
            printGroupInfo(threadGroup, 0);
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printWithLevels(String name, int levelOfThread) {
        for(int i = 0; i < levelOfThread; i++){
            System.out.print("  ");
        }
        System.out.print(name + System.lineSeparator());
    }

    private synchronized void printGroupInfo(ThreadGroup threadGroup, int levelOfThread) {
        printWithLevels(threadGroup.getName(), levelOfThread);

        Thread [] threadsInCurrentGroup = new Thread[threadGroup.activeCount()];
        int threadsCount = threadGroup.enumerate(threadsInCurrentGroup, false);
        if(threadsCount > 0){
            printWithLevels("Threads at " + threadGroup.getName() + ":", levelOfThread);
            for(int i = 0; i < threadsCount; i++ ){
                printWithLevels(threadsInCurrentGroup[i].getName(), levelOfThread + 1);
            }
        }
        ThreadGroup [] threadGroups = new ThreadGroup[threadGroup.activeGroupCount()];
        int groupsCount = threadGroup.enumerate(threadGroups, false);
        if(groupsCount > 0){
            printWithLevels("Thread groups in " + threadGroup.getName() + ":", levelOfThread);
            for(int i = 0; i < groupsCount; i++){
                printGroupInfo(threadGroups[i], levelOfThread + 1);
            }
        }
        notify();
    }
}
