package org.qiunet.utils.listener.event.data;

import org.qiunet.utils.listener.event.IEventData;

/***
 * 服务停止事件.
 * 属于通用. DuoDuo需要监听.
 *
 * @author qiunet
 * 2020-09-05 06:07
 **/
public class ServerShutdownCompleteEvent implements IEventData {

		private ServerShutdownCompleteEvent(){}

		/***因为没有参数. 所以可以使用单例 . 有参数的eventData 还是得自己new */
		private static final ServerShutdownCompleteEvent instance = new ServerShutdownCompleteEvent();

		public static void fireEvent(){
			instance.fireEventHandler();
		}
}
