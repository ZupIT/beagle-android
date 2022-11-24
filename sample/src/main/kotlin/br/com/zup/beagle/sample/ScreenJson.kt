/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.sample
const val SCREEN_BASE_URL = "https://gist.githubusercontent.com/Tiagoperes/" +
    "4579284bbace403f35c897dbc54a5d30/raw/2e1d5fa908bd678f837cdb4b69cb48eac9633148"
const val SCREEN2 = """{
  "_beagleComponent_": "beagle:screenComponent",
  "title": "Movies",
  "child": {
    "_beagleComponent_": "beagle:container",
    "context": {
      "id": "movies",
      "value": []
    },
    "onInit": [
      {
        "_beagleAction_": "beagle:sendRequest",
        "url": "${SCREEN_BASE_URL}/films1.json",
        "onSuccess": [
          {
            "_beagleAction_": "beagle:setContext",
            "contextId": "movies",
            "value": "@{onSuccess.data}"
          }
        ]
      }
    ],
    "children": [
      {
        "_beagleComponent_": "beagle:pullToRefresh",
        "context": {
          "id": "isRefreshing",
          "value": false
        },
        "onPull": [
          {
            "_beagleAction_": "beagle:setContext",
            "contextId": "isRefreshing",
            "value": true
          },
          {
            "_beagleAction_": "beagle:sendRequest",
            "url": "${SCREEN_BASE_URL}/films2.json",
            "onSuccess": [
              {
                "_beagleAction_": "beagle:setContext",
                "contextId": "movies",
                "value": "@{onSuccess.data}"
              }
            ],
            "onError": [
              {
                "_beagleAction_": "beagle:alert",
                "title": "Error",
                "message": "Error while sending request."
              }
            ],
            "onFinish": [
              {
                "_beagleAction_": "beagle:setContext",
                "contextId": "isRefreshing",
                "value": false
              }
            ]
          }
        ],
        "isRefreshing": "@{isRefreshing}",
        "color": "#0000FF",
        "child": {
          "_beagleComponent_": "beagle:listView",
          "style": {
            "size": {
              "height": {
                "value": 100,
                "type": "PERCENT"
              }
            }
          },
          "dataSource": "@{movies}",
          "templates": [
            {
              "view": {
                "_beagleComponent_": "beagle:container",
                "style": {
                  "margin": {
                    "all": {
                      "type": "REAL",
                      "value": 10
                    }
                  }
                },
                "children": [
                  {
                    "_beagleComponent_": "custom:text2",
                    "text": "@{item.Title} - @{item.Year}",
                    "textColor": "#FF0000"
                  },
                  {
                    "_beagleComponent_": "custom:text2",
                    "text": "@{item.Genre}"
                  },
                  {
                    "_beagleComponent_": "custom:text2",
                    "text": "@{item.Rating}"
                  },
                  {
                    "_beagleComponent_": "custom:text2",
                    "text": "@{item.Plot}"
                  }
                ]
              }
            }
          ]
        }
      },
      {
        "_beagleComponent_": "beagle:container",
        "style": {
          "backgroundColor": "#EEEEEE",
          "size": {
            "height": {
              "type": "PERCENT",
              "value": 30
            }
          }
        },
        "children": [
          {
            "_beagleComponent_": "custom:text2",
            "text": "My Static content"
          }
        ]
      }
    ]
  }
}"""

const val SCREEN3 = """{
  "_beagleComponent_": "beagle:screenComponent",
  "title": "Movies",
  "child": {
    "_beagleComponent_": "beagle:container",
    "context": {
      "id": "movies",
      "value": []
    },
    "onInit": [
      {
        "_beagleAction_": "beagle:sendRequest",
        "url": "${SCREEN_BASE_URL}/films1.json",
        "onSuccess": [
          {
            "_beagleAction_": "beagle:setContext",
            "contextId": "movies",
            "value": "@{onSuccess.data}"
          }
        ]
      }
    ],
    "children": [
      {
        "_beagleComponent_": "beagle:pullToRefresh",
        "context": {
          "id": "isRefreshing",
          "value": false
        },
        "onPull": [
          {
            "_beagleAction_": "beagle:setContext",
            "contextId": "isRefreshing",
            "value": true
          },
          {
            "_beagleAction_": "beagle:sendRequest",
            "url": "${SCREEN_BASE_URL}/films2.json",
            "onSuccess": [
              {
                "_beagleAction_": "beagle:setContext",
                "contextId": "movies",
                "value": "@{onSuccess.data}"
              }
            ],
            "onError": [
              {
                "_beagleAction_": "beagle:alert",
                "title": "Error",
                "message": "Error while sending request."
              }
            ],
            "onFinish": [
              {
                "_beagleAction_": "beagle:setContext",
                "contextId": "isRefreshing",
                "value": false
              }
            ]
          }
        ],
        "isRefreshing": "@{isRefreshing}",
        "color": "#0000FF",
        "child": {
          "_beagleComponent_": "beagle:listView",
          "style": {
            "size": {
              "height": {
                "value": 100,
                "type": "PERCENT"
              }
            }
          },
          "dataSource": "@{movies}",
          "templates": [
            {
              "view": {
                "_beagleComponent_": "beagle:container",
                "style": {
                  "margin": {
                    "all": {
                      "type": "REAL",
                      "value": 10
                    }
                  }
                },
                "children": [
                  {
                    "_beagleComponent_": "custom:text3",
                    "text": "@{item.Title} - @{item.Year}",
                    "textColor": "#FF0000"
                  },
                  {
                    "_beagleComponent_": "custom:text3",
                    "text": "@{item.Genre}"
                  },
                  {
                    "_beagleComponent_": "custom:text3",
                    "text": "@{item.Rating}"
                  },
                  {
                    "_beagleComponent_": "custom:text3",
                    "text": "@{item.Plot}"
                  }
                ]
              }
            }
          ]
        }
      },
      {
        "_beagleComponent_": "beagle:container",
        "style": {
          "backgroundColor": "#EEEEEE",
          "size": {
            "height": {
              "type": "PERCENT",
              "value": 30
            }
          }
        },
        "children": [
          {
            "_beagleComponent_": "custom:text3",
            "text": "My Static content"
          }
        ]
      }
    ]
  }
}"""