package org.hydracache.console

import com.thecoderscorner.groovychart.chart.ChartBuilder
import java.awt.Color
import org.jfree.chart.labels.PieToolTipGenerator
import org.jfree.chart.ChartPanel
import java.awt.BorderLayout

class SpaceDashboardController {
    // these will be injected by Griffon
    def model
    def view
    def hydraSpaceService

    void mvcGroupInit(Map args) {
        log.debug "Initializing SpaceDashboard MVC ..."
    }

    def update(nodes, storageInfo) {
        doLater {
            log.debug "Updating SpaceDashboard ..."

            model.serverNodes = nodes
            model.storageInfo = storageInfo

            model.updateOverview()

            ChartBuilder builder = new ChartBuilder()

            def pieChart = builder.piechart3d(title: "Hydra Space Overview", legend: true) {
                defaultPieDataset {
                    FreeMemory(model.freeMemoryPercentage)
                    UsedMemory(model.usedMemoryPercentage)
                }
                antiAlias = true
                backgroundPaint(Color.WHITE)

                pieplot {
                    sectionOutlinesVisible false
                    labelGap 0.02
                    toolTipGenerator({ dataset, key -> return "[${dataset} ${key}]" as String } as PieToolTipGenerator)

                    sectionPaint('FreeMemory', paint: Color.GREEN)
                    sectionPaint('UsedMemory', paint: Color.RED)
                }
            }

            def existingChart = null
            view.mainPanel.components.each{
                if(it instanceof ChartPanel)
                    existingChart = it
            }

            if(existingChart) view.mainPanel.remove(existingChart)

            view.mainPanel.add(new ChartPanel(pieChart.chart), BorderLayout.CENTER)
        }
    }

    def onHydraSpaceUpdated = { nodes, storageInfo ->
        log.debug "Event [HydraSpaceUpdated] received ..."

        update(nodes, storageInfo)

        log.debug "Event [HydraSpaceUpdated] processd"
    }
}