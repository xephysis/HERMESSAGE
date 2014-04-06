package ac.ajou.hermessageServerTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class MakeLog {

	MakeLog() {

	}

	public void logPrint(String messageID, String messageTYPE, String PDU)
			throws IOException {
		Date nowDate = new Date();
		File logFile = new File("/HERMESSAGE/log/" + String.valueOf(nowDate.getTime())
				+ "_" + messageID + "_" + messageTYPE + ".txt");
		FileOutputStream fosofLog = new FileOutputStream(logFile);

		fosofLog.write(PDU.getBytes());
		fosofLog.close();
		
		System.out.println("\n\n" + PDU + "\n\n");
	}

	public void logPrint(String messageID, String messageTYPE, InputStream data)
			throws IOException {
		Date nowDate = new Date();
		File logFile = new File("/HERMESSAGE/log/" + String.valueOf(nowDate.getTime())
				+ "_" + messageID + "_" + messageTYPE + ".txt");

		OutputStream out = new FileOutputStream(logFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = data.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();

	}

}