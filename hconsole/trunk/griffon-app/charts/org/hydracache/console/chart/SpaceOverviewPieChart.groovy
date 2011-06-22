package org.hydracache.console.chart

import java.awt.Color
import java.awt.Font
import org.jfree.chart.labels.PieToolTipGenerator
import org.apache.commons.io.FileUtils

piechart3d(title: "Hydra Space Overview", legend: true) {
    defaultPieDataset {
        FreeMemory(model.freeMemoryPercentage)
        UsedMemory(model.usedMemoryPercentage)
    }
    antiAlias = true
    backgroundPaint(Color.WHITE)

    pieplot {
        sectionOutlinesVisible false
        labelGap 0.02
        toolTipGenerator ({ dataset, key -> return "[${dataset} ${key}]" as String } as PieToolTipGenerator)

        sectionPaint('FreeMemory', paint: Color.GREEN)
        sectionPaint('UsedMemory', paint: Color.RED)
    }
}
