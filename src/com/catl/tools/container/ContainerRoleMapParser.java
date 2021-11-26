package com.catl.tools.container;

import java.io.IOException;
import java.io.InputStream;

public interface ContainerRoleMapParser {

	ContainerRoleMap parser(InputStream is) throws IOException;

}
