package account.utils

import groovy.transform.CompileStatic

class TestUtils {

        static String compressString(String str) {
            return str.replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
                    .replace(" ", "")
                    .trim()
        }

}
