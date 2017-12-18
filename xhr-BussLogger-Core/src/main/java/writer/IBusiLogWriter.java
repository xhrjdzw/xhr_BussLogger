package writer;

import model.BusinessLog;

/**
 * @author 徐浩然
 * @version IBusiLogWriter, 2017-12-15
 * 日志导出器，通过实现此接口来导出你的日志。
 */
public interface IBusiLogWriter {
    void write(BusinessLog businessLog);
}
