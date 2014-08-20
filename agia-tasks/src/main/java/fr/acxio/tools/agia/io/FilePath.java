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
 * <p>Path manipulation utilies.</p>
 * <p>The main purpose of FilePath is to be used into an
 * {@code EvaluationContextFactory} to handle paths easier.</p>
 * <p>FilePath can extract path elements and return each element by its index,
 * or return a sub-path, but it can do so with positive and negative index.</p>
 * <p>The elements of a path can be accessed by their index like in an array.
 * FilePath also accept negative index, starting from -1 which is the index
 * of the last element of the path, for example the file name. The index -2
 * will then reference the parent of the file, and so on.</p>
 * <p>The sub-paths can also be extract with negative index, starting from the
 * end of the path. For example, {@code getSubpath(-1, -2)} will return 
 * something like {@code "parentFolder/filename.ext"}.</p>
 * <p>The start and end index used in {@code getSubpath} can be independently
 * positive or negative, and they are not bound to the number of path elements:
 * if the path has 4 elements, the index 4 is equivalent to the index 0.</p>
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
    
    public String getSubpath(int sStart, int sEnd) {
        return getSubpath(sStart, sEnd, UNIX_SEPARATOR);
    }
    
    public String getSubpath(int sStart, int sEnd, String sPathSeparator) {
        String aResult = null;
        if (pathElements != null) {
            int aStartIndex = sStart % pathElements.length;
            aStartIndex = (aStartIndex < 0) ? aStartIndex + pathElements.length : aStartIndex;
            int aEndIndex = sEnd % pathElements.length;
            aEndIndex = (aEndIndex < 0) ? aEndIndex + pathElements.length : aEndIndex;
            if (aEndIndex < aStartIndex) {
                int aIndex = aStartIndex;
                aStartIndex = aEndIndex;
                aEndIndex = aIndex;
            }
            StringBuilder aStringBuilder = new StringBuilder();
            for(int i = aStartIndex; i <= aEndIndex; i++) {
                aStringBuilder.append(pathElements[i]);
                if (i < aEndIndex) {
                    aStringBuilder.append(sPathSeparator);
                }
            }
            aResult = aStringBuilder.toString();
        }
        return aResult;
    }
}
