#
# Copyright (C) 2022 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#
XPERIA_COMMON_SEPOLICY_DIR := hardware/sony/sepolicy/common

SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += \
	$(XPERIA_COMMON_SEPOLICY_DIR)/private
SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += \
	$(XPERIA_COMMON_SEPOLICY_DIR)/public
BOARD_VENDOR_SEPOLICY_DIRS += $(XPERIA_COMMON_SEPOLICY_DIR)/vendor
