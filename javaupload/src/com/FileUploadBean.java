package com;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
/**
 * Desc:文件上传类，实现将文件上传到服务器上
 * @author gjw
 * @version 1.0
 * 
 */
public class FileUploadBean {
	private String savePath, filepath, filename, contentType;
	private byte[] b;
	byte t;
	private Dictionary fields;
	/**
	 * 取得上传文件名
	 * @return 文件名称
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * 取得文件路径
	 * @return 文件路径
	 */
	public String getFilepath() {
		return filepath;
	}
	/**
	 * 设置保存路径
	 * @param savePath 保存的路径
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	/**
	 * 取得内容类型
	 * @return 上传文件内容类型
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * 取得字段域的值
	 * @param fieldName 字段名，区分大小写
	 * @return 此字段的值
	 */
	public String getFieldValue(String fieldName) {
		if (fields == null || fieldName == null)
			return null;
		return (String) fields.get(fieldName);
	}
	/**
	 * 设置文件名称
	 * @param s 上传的文件路
	 */
	private void setFilename(String s) {
		if (s == null)
			return;
		int pos = s.indexOf("filename=\"");
		if (pos != -1) {
			filepath = s.substring(pos + 10, s.length() - 1);
			// Windows浏览器发送完整的文件路径和名字
			// 但Linux/Unix和Mac浏览器只发送文件名字
			pos = filepath.lastIndexOf("\\");
			if (pos != -1)
				filename = filepath.substring(pos + 1);
			else
				filename = filepath;
		}
	}
	/**
	 * 设置文件内容类型
	 * @param s 内容类型
	 */
	private void setContentType(String s) {
		if (s == null)
			return;
		int pos = s.indexOf(": ");
		if (pos != -1)
			contentType = s.substring(pos + 2, s.length());
	}
	/**
	 * 取得Request对象的字节流
	 * @param request HttpServletRequest对象
	 */
	public void getByte(HttpServletRequest request)
	{
		DataInputStream is;
		int i = 0;
		try
		{
			is = new DataInputStream(request.getInputStream());
			b = new byte[request.getContentLength()];
			while (true)
			{
				try
				{
					t = is.readByte();
					b[i] = t;
					i++;
				}
				catch (EOFException e)
				{
					break;
				}
			}
			is.close();
		}
		catch (IOException e)
		{
		}
	}
	/**
	 * 
	 * @param request
	 * @throws IOException
	 */
	public void doUpload1(HttpServletRequest request) throws
	IOException {
		byte[] line = new byte[128];
		FileOutputStream os = new FileOutputStream("c:\\Demo.out");
		ServletInputStream in = request.getInputStream();
		getByte(request);
		String temp = "";
		temp = new String(b, "ISO8859_1");
		byte[] img = temp.getBytes("ISO8859_1");
		for (int i = 0; i < img.length; i++)
		{
			os.write(img[i]);
		}
		os.close();
	}
	/**
	 * 以GB2312转码，上传文件
	 * @param request request对象
	 * @throws IOException 读取文件异常
	 */
	public void doUpload(HttpServletRequest request) throws IOException {
		request.setCharacterEncoding("GB2312");
		ServletInputStream in = request.getInputStream();
		byte[] line = new byte[1280];
		int i = in.readLine(line, 0, 1280);
		if (i < 3)
			return;
		int boundaryLength = i - 2;
		String boundary = new String(line, 0, boundaryLength); // -2丢弃换行字符
		fields = new Hashtable();
		while (i != -1) {
			String newLine = new String(line, 0, i);
			if (newLine.startsWith("Content-Disposition: form-data; name=\"")) 
			{
				if (newLine.indexOf("filename=\"") != -1) 
				{
					setFilename(new String(line, 0, i - 2));
					if (filename == null)
						return;
					// 文件内容
					i = in.readLine(line, 0, 1280);
					setContentType(new String(line, 0, i - 2));
					i = in.readLine(line, 0, 1280);
					// 空行
					i = in.readLine(line, 0, 1280);
					newLine = new String(line, 0, i, "ISO8859_1");
					FileOutputStream pw = new FileOutputStream(
							(savePath == null ? "" : savePath) + filename);
					// PrintWriter pw = new PrintWriter(new BufferedWriter(new
					// FileWriter((savePath==null? "" : savePath) + filename)));
					while (i != -1 && !newLine.startsWith(boundary)) {
						// 文件内容的最后一行包含换行字符
						// 因此我们必须检查当前行是否是最
						// 后一行
						i = in.readLine(line, 0, 1280);
						if ((i == boundaryLength + 2 || i == boundaryLength + 4)&& (new String(line, 0, i).startsWith(boundary)))
							pw.write(newLine.substring(0, newLine.length() - 2).getBytes("ISO8859_1"));
						else
							pw.write(newLine.getBytes("ISO8859_1"));
						newLine = new String(line, 0, i, "ISO8859_1");
					}
					pw.close();
				}
				else 
				{
					// 普通表单输入元素
					// 获取输入元素名字
					int pos = newLine.indexOf("name=\"");
					String fieldName = newLine.substring(pos + 6, newLine.length() - 3);
					i = in.readLine(line, 0, 1280);
					i = in.readLine(line, 0, 1280);
					newLine = new String(line, 0, i);
					StringBuffer fieldValue = new StringBuffer(1280);
					while (i != -1 && !newLine.startsWith(boundary)) {
						// 最后一行包含换行字符
						// 因此我们必须检查当前行是否是最后一行
						i = in.readLine(line, 0, 1280);
						if ((i == boundaryLength + 2 || i == boundaryLength + 4)&& (new String(line, 0, i).startsWith(boundary)))
							fieldValue.append(newLine.substring(0, newLine.length() - 2));
						else
							fieldValue.append(newLine);
						newLine = new String(line, 0, i);
					}
					fields.put(fieldName, fieldValue.toString());
				}
			}
			i = in.readLine(line, 0, 1280);
		}
	}
}
