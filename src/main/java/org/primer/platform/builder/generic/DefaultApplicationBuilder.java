/**
 * Copyright 2011 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primer.platform.builder.generic;

import org.primer.platform.GenericApplicationSpecification;
import org.primer.platform.GenericApplication;
import org.primer.platform.GenericApplicationFactory;

/**
 * <p>Builder to provide the following to {@link GenericApplicationFactory}:</p>
 * <ul>
 * <li>Builds a particular variant of the {@link GenericApplication} suitable for the current platform</li>
 * </ul>
 *
 * @since 0.2.0
 *  
 */
public class DefaultApplicationBuilder {
    public DefaultApplication build(GenericApplicationSpecification specification) {
        return new DefaultApplication();
    }
}