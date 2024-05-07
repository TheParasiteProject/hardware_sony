/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "vendor.lineage.powershare@1.0-service.sony"

#include "PowerShare.h"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>

using ::android::base::ReadFileToString;
using ::android::base::WriteStringToFile;

namespace vendor {
namespace lineage {
namespace powershare {
namespace V1_0 {
namespace implementation {

Return<bool> PowerShare::isEnabled() {
    std::string value;

    // Read file and trim any whitespaces from value
    auto ret = ReadFileToString(WIRELESS_TX_ENABLE_PATH, &value);
    value = android::base::Trim(value);

    LOG(INFO) << __func__ << ": wireless_tx_enable: " << value;

    return ret && value != "0";
}

Return<bool> PowerShare::setEnabled(bool enable) {
    // Log writing failures,
    // but don't return early after that,
    // as we will return result of isEnabled() anyways.
    if (!WriteStringToFile(enable ? "1" : "0", WIRELESS_TX_ENABLE_PATH, true)) {
        LOG(ERROR) << "Failed to write to file " << WIRELESS_TX_ENABLE_PATH << "!";
    }

    // Return result of isEnabled() to handle situations
    // when reverse charging status changed
    // by kernel or other hals after writing.
    return isEnabled();
}

Return<uint32_t> PowerShare::getMinBattery() {
    return 0;
}

Return<uint32_t> PowerShare::setMinBattery(uint32_t /*minBattery*/) {
    return getMinBattery();
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace powershare
}  // namespace lineage
}  // namespace vendor
