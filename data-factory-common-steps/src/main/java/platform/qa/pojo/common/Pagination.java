/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.pojo.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Pagination {
    private int limit;
    private int offset;

    public String getLimit() {
        return String.valueOf(limit);
    }

    public String getOffset() {
        return String.valueOf(offset);
    }
}
