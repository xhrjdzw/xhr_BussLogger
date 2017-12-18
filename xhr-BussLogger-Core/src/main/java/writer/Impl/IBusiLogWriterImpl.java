package writer.Impl;

import model.BusinessLog;
import writer.IBusiLogWriter;

/**
 * @author 徐浩然
 * @version IBusiLogWriterImpl, 2017-12-15
 */
public class IBusiLogWriterImpl implements IBusiLogWriter
{
    @Override
    public void write(BusinessLog businessLog)
    {
        //测试日志打印
        System.out.println(businessLog);
    }
}
