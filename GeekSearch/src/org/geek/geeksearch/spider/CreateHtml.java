package org.geek.geeksearch.spider;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CreateHtml {

	public void OutputFile(String path, String data) {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			
			FileOutputStream fw = new FileOutputStream(path); 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fw,"GB2312"));
		
			bw.write(data);
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}