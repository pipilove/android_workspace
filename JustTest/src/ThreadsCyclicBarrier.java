import java.util.concurrent.CyclicBarrier;

public class ThreadsCyclicBarrier {
	private static CyclicBarrier barrier;

	public static void main(String args[]) {
		barrier = new CyclicBarrier(3, new Runnable() {// 设置了3个barrier
					public void run() {
						System.out.println("全部线程执行完毕！");
					}
				});
		test();
	}

	public static void test() {
		for (int i = 0; i < 3; i++) {
			new Thread() {
				public void run() {
					try {
						System.out.println("thread finish");
						barrier.await();// 减少一个barrier
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}.start();
		}
	}

}