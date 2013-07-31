BigFileUploadJava
====================

### [☆ English](#english) ###
### [☆ 中文](#chinese) ###

Basics <a id="english" name="english"></a>
-----------------------------------
Upload big file using java.

### Process Flow
Basically, read a big file into small parts and upload. When all file parts upload is complete, combine at server.

#### Client

1. Read the big file into several small parts. Considering I/O contention, use just one thread; Considering memory usage, read file part by part into fixed size queue.
2. Upload every readed file part. Usually multiple threads would be better, but can't too much, default threads count is 5.
3. After all parts uploaded, notify server to combine.
4. Can retry the specific parts only if failed to process.

#### Server

1. Save recieved file parts.
2. Combine all parts by notification.

#### Others

- Producer/Consumer pattern.
- Communicate read and upload processes by BlockingQueue.
- Uploading is using Apache HttpComponents currently. There can be other implementations.
- Can be used in the android. Please refer to [BigFileUploadAndroid](https://github.com/clxy/BigFileUploadAndroid)。


### Usage

#### Configuration
Please refer to [cn.clxy.upload.Config class](https://github.com/clxy/BigFileUploadJava/blob/master/src/main/java/cn/clxy/upload/Config.java)

#### Upload
	UploadFileService service = new UploadFileService(yourFileName);
	service.upload();

#### Retry failed parts
	UploadFileService service = new UploadFileService(yourFileName);
	service.retry(1, 2);

### Server
Because it is via HTTP, so it can be in any language, such as Java, PHP, Python, etc.
Here is a java example.

	...
	try (FileOutputStream dest = new FileOutputStream(destFile, true)) {

		FileChannel dc = dest.getChannel();// the final big file。
		for (long i = start; i < count; i++) {
			File partFile = new File(destFileName + "." + i);// every small parts。
			if (!partFile.exists() && i != 0) {
				return OK;
			}
			try (FileInputStream part = new FileInputStream(partFile)) {
				FileChannel pc = part.getChannel();
				pc.transferTo(0, pc.size(), dc);// combine。
			}
			partFile.delete();
		}
	} catch (Exception e) {
		log.error("combine failed.", e);
		return BAD;
	}
	return OK;

* * ** * ** * ** * ** * ** * ** * ** * ** * *


概要<a id="chinese" name="chinese"></a>
-----------------------------------
使用Java上传大文件的实现。

### 处理流程
基本上是将大文件拆成小块，读取，上传。当所有文件块上传完成后，合并。

#### 客户端

1. 读取文件到小的文件块。考虑到I/O竞争只用单线程；考虑到内存消耗采取分批读取。
2. 将读取的文件块上传。通常多个线程会好些，但是太多又不行，默认用5线程上传。
3. 所有文件块全部上传后，通知服务器合并。
4. 如果有部分文件块处理失败，可以重试失败部分。

#### 服务器

1. 收到文件块后直接保存。
2. 收到通知后合并所有文件块。

#### 其他

- 使用生产/消费者模式。
- 读取和上传的通信使用BlockingQueue。
- 目前上传用Apache HttpComponents。可以有其他实现。
- 可以用在Android上。请参考[BigFileUploadAndroid](https://github.com/clxy/BigFileUploadAndroid)。


### 使用

#### 配置
请参考 [cn.clxy.upload.Config class](https://github.com/clxy/BigFileUploadJava/blob/master/src/main/java/cn/clxy/upload/Config.java).

#### 上传
	UploadFileService service = new UploadFileService(yourFileName);
	service.upload();

#### 重试失败部分
	UploadFileService service = new UploadFileService(yourFileName);
	service.retry(1, 2);

### 服务器
由于是经由HTTP上传，所以可以是任何语言如Java，PHP，Python等。
下面是合并文件的Java实例。

	...
	try (FileOutputStream dest = new FileOutputStream(destFile, true)) {

		FileChannel dc = dest.getChannel();// 最终的大文件。
		for (long i = start; i < count; i++) {
			File partFile = new File(destFileName + "." + i);// 每个文件块。
			if (!partFile.exists() && i != 0) {
				return OK;
			}
			try (FileInputStream part = new FileInputStream(partFile)) {
				FileChannel pc = part.getChannel();
				pc.transferTo(0, pc.size(), dc);// 合并。
			}
			partFile.delete();
		}
	} catch (Exception e) {
		log.error("combine failed.", e);
		return BAD;
	}
	return OK;
