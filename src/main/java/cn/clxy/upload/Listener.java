package cn.clxy.upload;

public interface Listener {

	void onStart(Object info);

	void onRead(Object info);

	void onUpload(Object info);

	void onPartDone(Object info);

	void onNotify();

	void onFail(Object info);

	void onSuccess();

	public static class Default implements Listener {

		@Override
		public void onStart(Object info) {
			onMessage("Start uploading " + info + " part(s).");
		}

		@Override
		public void onRead(Object info) {
			onMessage("Reading part " + info + ".");
		}

		@Override
		public void onUpload(Object info) {
			onMessage("Uploading part " + info + ".");
		}

		@Override
		public void onPartDone(Object info) {
			onMessage("Part " + info + " is done.");
		}

		@Override
		public void onNotify() {
			onMessage("Notifying.");
		}

		@Override
		public void onFail(Object info) {
			onMessage(info + " failed.");
		}

		@Override
		public void onSuccess() {
			onMessage("Success.");
		}

		protected void onMessage(String msg) {
			System.out.println(msg);
		}
	}
}
