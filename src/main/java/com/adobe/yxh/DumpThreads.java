package com.adobe.yxh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
 
/**
 * Code which writes a stack dump for all threads to a response.
 */

@Component(metatype = true, label = "Thread Dump",
description = "Servlet for dump Thread")
@Service
@Property(name = "sling.servlet.paths", value = "/bin/log/threaddump", propertyPrivate = true)

public class DumpThreads extends SlingAllMethodsServlet{
 
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
        
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException{
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
        Map<Long, ThreadInfo> threadInfoMap = new HashMap<Long, ThreadInfo>();
        for (ThreadInfo threadInfo : threadInfos) {
                threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
        }
         dumpTraces(mxBean, threadInfoMap, response.getWriter());
         response.flushBuffer();
	}
    private static void dumpTraces(ThreadMXBean mxBean, Map<Long, ThreadInfo> threadInfoMap, Writer writer)
                    throws IOException {
            Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
            writer.write("Dump of " + stacks.size() + " thread at "
                        + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(System.currentTimeMillis())) + "\n\n");
        for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
                Thread thread = entry.getKey();
                writer.write("\"" + thread.getName() + "\" prio=" + thread.getPriority() + " tid=" + thread.getId() + " "
                                + thread.getState() + " " + (thread.isDaemon() ? "deamon" : "worker") + "\n");
                ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
                if (threadInfo != null) {
                        writer.write("    native=" + threadInfo.isInNative() + ", suspended=" + threadInfo.isSuspended()
                                        + ", block=" + threadInfo.getBlockedCount() + ", wait=" + threadInfo.getWaitedCount() + "\n");
                        writer.write("    lock=" + threadInfo.getLockName() + " owned by " + threadInfo.getLockOwnerName()
                                        + " (" + threadInfo.getLockOwnerId() + "), cpu="
                                        + (mxBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L) + ", user="
                                        + (mxBean.getThreadUserTime(threadInfo.getThreadId()) / 1000000L) + "\n");
                }
                for (StackTraceElement element : entry.getValue()) {
                        writer.write("        ");
                        writer.write(element.toString());
                        writer.write("\n");
                }
                writer.write("\n");
            }
    }
}