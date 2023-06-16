/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:Suppress("SpellCheckingInspection")

package io.github.jisungbin.covid19center.datasource

import io.github.jisungbin.covid19center.model.SimpleLatLng
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.github.jisungbin.covid19center.util.buildDate
import org.intellij.lang.annotations.Language

object DummyResponse {
  @Language("json")
  const val Json = """
{
  "currentCount": 3,
  "matchCount": 30,
  "totalCount": 30,
  "page": 1,
  "perPage": 3,
  "data": [
    {
      "address": "그들의 인간은 타오르고 할지라도 아니더면",
      "centerName": "사막이다.",
      "centerType": "청춘의",
      "createdAt": "2021-03-15 00:03:43",
      "facilityName": "들은 바이며, 철환하였는가?",
      "id": 1,
      "lat": "37.12345",
      "lng": "127.54321",
      "org": "",
      "phoneNumber": "02-1234-1234",
      "sido": "서울특별시",
      "sigungu": "성동구",
      "updatedAt": "2021-07-16 04:55:09",
      "zipCode": "11111"
    },
    {
      "address": "이상은 얼마나 커다란 충분히",
      "centerName": "이상의 물방아 있는가",
      "centerType": "용기가",
      "createdAt": "2021-03-15 00:03:43",
      "facilityName": "그와 꽃 청춘",
      "id": 2,
      "lat": "35.12641",
      "lng": "129.72565",
      "org": "",
      "phoneNumber": "051-444-1111",
      "sido": "부산광역시",
      "sigungu": "부산진구",
      "updatedAt": "2023-01-12 15:53:01",
      "zipCode": "89076"
    },
    {
      "address": "할지라도 그것을 든 생의 그들을",
      "centerName": "들어 오아이스도 힘차게",
      "centerType": "현저하게",
      "createdAt": "2021-03-15 00:03:43",
      "facilityName": "가장 창공에 황금시대다",
      "id": 3,
      "lat": "37.33333",
      "lng": "126.99686",
      "org": "",
      "phoneNumber": "032-111-0000",
      "sido": "인천광역시",
      "sigungu": "연수구",
      "updatedAt": "2019-11-29 21:21:21",
      "zipCode": "15313"
    }
  ]
}
"""

  val CenterItemList = listOf(
    CovidCenterItem(
      id = 1,
      centerType = "청춘의",
      address = "그들의 인간은 타오르고 할지라도 아니더면",
      latlng = SimpleLatLng(37.12345, 127.54321),
      centerName = "사막이다.",
      facilityName = "들은 바이며, 철환하였는가?",
      phoneNumber = "02-1234-1234",
      updatedAt = buildDate(
        year = 2021,
        month = 6,
        day = 16,
        hour = 4,
        minute = 55,
        second = 9,
      ),
    ),
    CovidCenterItem(
      id = 2,
      centerType = "용기가",
      address = "이상은 얼마나 커다란 충분히",
      latlng = SimpleLatLng(35.12641, 129.72565),
      centerName = "이상의 물방아 있는가",
      facilityName = "그와 꽃 청춘",
      phoneNumber = "051-444-1111",
      updatedAt = buildDate(
        year = 2023,
        month = 0,
        day = 12,
        hour = 15,
        minute = 53,
        second = 1,
      ),
    ),
    CovidCenterItem(
      id = 3,
      centerType = "현저하게",
      address = "할지라도 그것을 든 생의 그들을",
      latlng = SimpleLatLng(37.33333, 126.99686),
      centerName = "들어 오아이스도 힘차게",
      facilityName = "가장 창공에 황금시대다",
      phoneNumber = "032-111-0000",
      updatedAt = buildDate(
        year = 2019,
        month = 10,
        day = 29,
        hour = 21,
        minute = 21,
        second = 21,
      ),
    ),
  )
}
