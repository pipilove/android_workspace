import java.util.concurrent.CyclicBarrier;

public class ThreadsCyclicBarrier {
	private static CyclicBarrier barrier;

	public static void main(String args[]) {
		barrier = new CyclicBarrier(3, new Runnable() {// ������3��barrier
					public void run() {
						System.out.println("ȫ���߳�ִ����ϣ�");
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
						barrier.await();// ����һ��barrier
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}.start();
		}
	}

}