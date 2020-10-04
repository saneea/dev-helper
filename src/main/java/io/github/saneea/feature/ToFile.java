package io.github.saneea.feature;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToFile implements Feature, Feature.Err.Bin.Stream {

	interface ExitCode {
		int OK = 0;
	}

	interface Arg {
		int FILE_NAME = 0;
		int COMMAND_LINE = 1;
	}

	private OutputStream err;

	@Override
	public String getShortDescription() {
		return "save output of external process to file";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		String[] args = context.getArgs();
		String outFileName = args[Arg.FILE_NAME];
		String commandLine = args[Arg.COMMAND_LINE];

		Process forkProc = Runtime.getRuntime().exec(commandLine);

		try (ByteArrayBuffer stdOutBuffer = getStdOutFromProc(forkProc, err)) {
			int exitCode = forkProc.waitFor();

			if (exitCode == ExitCode.OK) {
				bufferToFile(stdOutBuffer, outFileName);
			} else {
				System.exit(exitCode);
			}
		}
	}

	private void bufferToFile(ByteArrayBuffer buffer, String fileName) throws IOException {
		try (OutputStream outFileStream = new BufferedOutputStream(new FileOutputStream(fileName))) {
			buffer.writeTo(outFileStream);
		}
	}

	private ByteArrayBuffer getStdOutFromProc(Process forkProc, OutputStream err)
			throws InterruptedException, ExecutionException, IOException {

		try (InputStream forkProcOut = new BufferedInputStream(forkProc.getInputStream()); //
				InputStream forkProcErr = new BufferedInputStream(forkProc.getErrorStream())) {

			ExecutorService executorService = Executors.newFixedThreadPool(2);
			try {

				Future<ByteArrayOutputStream> transferStdOut = executorService.submit(transferToBuffer(forkProcOut));
				Future<OutputStream> transferStdErr = executorService.submit(transfer(forkProcErr, err));

				waitFuture(transferStdErr);
				return ByteArrayBuffer.of(transferStdOut.get());
			} finally {
				executorService.shutdown();
			}
		}
	}

	private void waitFuture(Future<?> future) throws InterruptedException, ExecutionException {
		future.get();
	}

	private static Callable<ByteArrayOutputStream> transferToBuffer(InputStream in) {
		return transfer(in, new ByteArrayOutputStream());
	}

	private static <OutputStreamType extends OutputStream> Callable<OutputStreamType> transfer(InputStream in,
			OutputStreamType out) {
		return () -> {
			in.transferTo(out);
			return out;
		};
	}

	private interface ByteArrayBuffer extends Closeable {
		void writeTo(OutputStream out) throws IOException;

		static ByteArrayBuffer of(ByteArrayOutputStream byteArrayOutputStream) {
			return new ByteArrayBuffer() {
				@Override
				public void writeTo(OutputStream out) throws IOException {
					byteArrayOutputStream.writeTo(out);
				}

				@Override
				public void close() throws IOException {
					byteArrayOutputStream.close();
				}
			};
		}
	}

	@Override
	public void setErr(OutputStream err) {
		this.err = err;
	}
}
