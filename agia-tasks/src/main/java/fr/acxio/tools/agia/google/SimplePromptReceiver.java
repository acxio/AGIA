package fr.acxio.tools.agia.google;

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

import java.io.IOException;

import com.google.api.client.extensions.java6.auth.oauth2.AbstractPromptReceiver;

public class SimplePromptReceiver extends AbstractPromptReceiver {

    private String redirectUri = "http://localhost/";

    public void setRedirectUri(String sRedirectUri) {
        redirectUri = sRedirectUri;
    }

    @Override
    public String getRedirectUri() throws IOException {
        return redirectUri;
    }
}
