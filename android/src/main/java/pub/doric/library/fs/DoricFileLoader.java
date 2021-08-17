/*
 * Copyright [2019] [Doric.Pub]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.doric.library.fs;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import pub.doric.Doric;
import pub.doric.async.AsyncResult;
import pub.doric.loader.IDoricJSLoader;

/**
 * @Description: pub.doric.extension.fs
 * @Author: pengfei.zhou
 * @CreateDate: 2020/9/19
 */

public class DoricFileLoader implements IDoricJSLoader {
    @Override
    public boolean filter(String source) {
        return source.startsWith("content://") || source.startsWith("file://") || source.startsWith("/");
    }

    @Override
    public AsyncResult<String> request(String source) {
        AsyncResult<String> result = new AsyncResult<>();
        InputStream inputStream = null;
        try {
            if (source.startsWith("content://")) {
                Uri uri = Uri.parse(source);
                ContentResolver resolver = Doric.application().getContentResolver();

                inputStream = resolver.openInputStream(uri);
            } else {
                if (source.startsWith("file://")) {
                    source = source.substring("file://".length());
                }
                inputStream = new FileInputStream(source);
            }
            assert inputStream != null;
            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            result.setResult(new String(buffer));
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
