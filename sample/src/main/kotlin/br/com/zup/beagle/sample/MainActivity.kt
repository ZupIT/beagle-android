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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.utils.loadView
import br.com.zup.beagle.android.utils.newServerDrivenIntent
import br.com.zup.beagle.android.view.ServerDrivenActivity
import br.com.zup.beagle.sample.constants.SAMPLE_ENDPOINT
import br.com.zup.beagle.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        renderScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navigation_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuSelected(itemSelected = item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun menuSelected(itemSelected: Int) {
        when (itemSelected) {
            R.id.remote_server -> startActivity(
                newServerDrivenIntent<ServerDrivenActivity>(
                    requestData = RequestData(SAMPLE_ENDPOINT),
                    beagleSdk = BeagleSetup()
                )
            )
        }
    }

    private fun renderScreen() {
        binding.fragmentContent.loadView(
            this,
            config = BeagleSetup(),
            screenJson = """{
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
        "url": "https://gist.githubusercontent.com/Tiagoperes/4579284bbace403f35c897dbc54a5d30/raw/2e1d5fa908bd678f837cdb4b69cb48eac9633148/films1.json",
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
            "url": "https://gist.githubusercontent.com/Tiagoperes/4579284bbace403f35c897dbc54a5d30/raw/2e1d5fa908bd678f837cdb4b69cb48eac9633148/films2.json",
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
                    "_beagleComponent_": "beagle:text",
                    "text": "@{item.Title} - @{item.Year}",
                    "textColor": "#FF0000"
                  },
                  {
                    "_beagleComponent_": "beagle:text",
                    "text": "@{item.Genre}"
                  },
                  {
                    "_beagleComponent_": "beagle:text",
                    "text": "@{item.Rating}"
                  },
                  {
                    "_beagleComponent_": "beagle:text",
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
            "_beagleComponent_": "beagle:text",
            "text": "My Static content"
          }
        ]
      }
    ]
  }
}"""
        )
    }
}
