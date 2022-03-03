/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.metamodel.excel;

import java.util.Locale;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class DefaultLocaleRule extends TestWatcher {
    private Locale defaultLocale;
    private Locale locale;

    public DefaultLocaleRule(Locale locale) {
        this.locale = locale;
    }

    @Override
    protected void starting(Description description) {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(locale);
    }

    @Override
    protected void finished(Description description) {
        Locale.setDefault(defaultLocale);
    }
}