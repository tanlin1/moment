package utils.android;

import java.io.*;

/**
 * Created by HP on 2014/8/2.
 */
public class Read {
	public static String read(InputStream in) throws IOException {
		String temp;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"));
		while((temp = br.readLine()) != null){
			sb.append(temp);
		}
		br.close();
		return sb.toString();
	}
}
