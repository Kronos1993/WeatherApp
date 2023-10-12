package com.kronos.data.local.user_custom_location.mapper

import com.kronos.data.local.user_custom_location.entity.UserCustomLocationEntity
import com.kronos.domian.model.UserCustomLocation


internal fun UserCustomLocationEntity.toDomain(): UserCustomLocation =
    UserCustomLocation(
        id = id,
        cityName = cityName,
        isCurrent = isCurrent,
        isSelected = isSelected,
        lat = lat,
        lon = lon
    )

internal fun UserCustomLocation.toEntity(): UserCustomLocationEntity =
    UserCustomLocationEntity(
        id = id,
        cityName = cityName,
        isCurrent = isCurrent,
        isSelected = isSelected,
        lat = lat!!,
        lon = lon!!
    )

