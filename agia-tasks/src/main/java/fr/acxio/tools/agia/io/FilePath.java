package fr.acxio.tools.agia.io;

/*
 * Copyright 2014 Acxio
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * <p>
 * Path manipulation utilies.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class FilePath {

    private static final String UNIX_SEPARATOR = "/";

    private String[] pathElements;

    public FilePath() {
    }

    public FilePath(String sPath) {
        if (sPath != null) {
            extractElements(sPath);
        }
    }

    public FilePath(File sFile) {
        if (sFile != null) {
            extractElements(sFile.getAbsolutePath());
        }
    }

    private void extractElements(String sPath) {
        pathElements = FilenameUtils.separatorsToUnix(sPath).split(UNIX_SEPARATOR);
    }

    public String getElement(int sIndex) {
        String aResult = null;
        if (pathElements != null) {
            int aRollingIndex = sIndex % pathElements.length;
            aResult = (aRollingIndex < 0) ? pathElements[aRollingIndex + pathElements.length] : pathElements[aRollingIndex];
        }
        return aResult;
    }

    public static FilePath valueOf(String sPath) {
        return new FilePath(sPath);
    }

    public static FilePath valueOf(File sFile) {
        return new FilePath(sFile);
    }
}
