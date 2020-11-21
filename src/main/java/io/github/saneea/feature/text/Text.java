package io.github.saneea.feature.text;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.feature.multi.MultiFeatureBase;

public class Text extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "plain text processing";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.multiFeature(//
						"case", //
						"transform to upper/lower case", //
						new AliasesBuilder()//
								.feature("upper", ConvertTextCase.Upper::new)//
								.feature("lower", ConvertTextCase.Lower::new)//
								.build())//
				.multiFeature(//
						"chunker", //
						"split text to chunks or join chunks again", //
						new AliasesBuilder()//
								.feature("split", Split::new)//
								.feature("join", JoinLines::new)//
								.build())//
				.feature("toCharArray", ToCharArray::new)//
				.feature("trim", Trim::new)//
				.feature("newLine", AddNewLine::new)//
				.feature("pipe", Pipe::new)//
				.build();
	}
}
