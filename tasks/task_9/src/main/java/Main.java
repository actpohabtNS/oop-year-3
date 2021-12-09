public class Main {
    public static void main(String[] args) {
        ThreadGroupService threadGroupService = new ThreadGroupService();

        ThreadGroup threadGroup1 = new ThreadGroup("Group I");
        ThreadGroup threadGroup2 = new ThreadGroup(threadGroup1, "Group II");
        ThreadGroup threadGroup3 = new ThreadGroup(threadGroup2, "Group III");

        new Thread(threadGroup1, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread 1").start();


        new Thread(threadGroup2, () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread 2").start();

        new Thread(threadGroup2, () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread 3").start();

        new Thread(threadGroup3, () -> {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread 4").start();

        threadGroupService.printTreadsInfo(threadGroup1);
        threadGroupService.printTreadsInfo(threadGroup2);
        threadGroupService.printTreadsInfo(threadGroup3);
    }
}
