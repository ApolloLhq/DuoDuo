package org.qiunet.log.record.msg;

import com.google.common.collect.Lists;
import org.qiunet.log.record.enums.ILogRecordType;
import org.qiunet.utils.exceptions.CustomException;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/***
 * 日志的基类.
 *
 * @author qiunet
 * 2020-03-30 07:52
 **/
public abstract class LogRecordMsg<LogType extends Enum<LogType> & ILogRecordType<LogType>> implements ILogRecordMsg<LogType>{
	private final List<LogRowData> dataList = Lists.newLinkedList();

	private final AtomicBoolean logged = new AtomicBoolean();
	protected final LogType eventLogType;
	protected final long createTime;

	protected LogRecordMsg(LogType eventLogType) {
		this.createTime = System.currentTimeMillis();
		this.eventLogType = eventLogType;
	}

	@Override
	public void forEachData(Consumer<LogRowData> consumer) {
		if (logged.compareAndSet(false, true)) {
			this.fillLogRecordMsg();
		}
		this.dataList.forEach(consumer);
	}

	@Override
	public void append(String key, Object val) {
		if (logged.get()) {
			throw new CustomException("Already output message!");
		}
		this.dataList.add(LogRowData.valueOf(key, val));
	}

	/**
	 * 填充日志
	 */
	protected abstract void fillLogRecordMsg();
	@Override
	public long createTime() {
		return createTime;
	}
	@Override
	public LogType logType() {
		return eventLogType;
	}
}
