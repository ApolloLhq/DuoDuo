package org.qiunet.test.handler.proto;

import org.qiunet.flash.handler.util.proto.GeneratorProtoFile;
import org.qiunet.flash.handler.util.proto.ProtoGeneratorModel;
import org.qiunet.utils.scanner.ClassScanner;
import org.qiunet.utils.scanner.ScannerType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/***
 *
 *
 * @author qiunet
 * 2020-09-25 12:54
 */
public class TestProtoFileCreate {

	public static void main(String[] args) throws Exception {
		Path path = Paths.get("/Users/qiunet/Desktop/proto");
		ClassScanner.getInstance(ScannerType.GENERATOR_PROTO).scanner("org.qiunet");

		GeneratorProtoFile.generator(path.toFile(), ProtoGeneratorModel.GROUP_BY_MODULE);
	}
}
