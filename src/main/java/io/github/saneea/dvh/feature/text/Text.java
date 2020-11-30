package io.github.saneea.dvh.feature.text;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class Text extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("plain text processing");
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
				.feature("slash", SlashReplacer::new)//
				.build();
	}
}
