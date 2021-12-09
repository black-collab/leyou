package com.leyou.upload.config;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
@Import(FdfsClientConfig.class)
//防止bean冲突，如果已经注入相同的bean，就使用已有的
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FastClientConfigImport {

}
