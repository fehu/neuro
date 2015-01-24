package feh.tec.neuro.export

import feh.tec.neuro.definition.NetworkDefinition.{Synapse, NeuronSelect}
import feh.tec.neuro.dsl.{Network, Neuro}

import feh.tec.neuro.definition.NetworkDefinition
import feh.dsl.graphviz._
import feh.util.Path._

object Test extends App{
  val nNetwork = Neuro define new Network{
    Layer(0, neurons = 4)(
      connect(
        1 to 1~(1 to 3),
        2 to allOfLayer(2),
        3 to allOfLayer(2),
        4 to (1~5 and 2~(3, 4))
      )
    )

    Layer(1, neurons = 5)(
      connect(
        1 to 2~1,
        2 to 2~(1, 2),
        3 to 2~(2, 3),
        4 to 2~4,
        5 to 2~4
      )
    )

    Layer(2, neurons = 4)(
      connect(
        3 to 0~1 withDelay 1,
        4 to 0~2 withDelay 2
      )
    )

    Layer(3, neurons = 0)(NoConnections)

    def inputs = 0~3 and 0~4
    def outputs = 2~(1, 2)

  }

  val dot = ToDot(nNetwork)
  GraphvizExec.writeAndExecGraphviz(`.` / "NNTest.dot", dot)(OutFormat.Png, Prog.Dot)
}

// todo: delays, inputs, outputs
object ToDot extends DotDslImpl(4){
  def apply(nd: NetworkDefinition, name: String = "Neural Network"): String = {
    val layers = nd.neurons.groupBy(_.layer).toSeq.map{
      case (layer, neurons) => Cluster("Layer " + layer, attr.autoLabel)(
        neurons.map(_.toString.node): _*
      )
    }
    val synapses = nd.synapses.toList.map{
      case Synapse(from, to, delay) => from.toString -> to.toString
    }

    write.graph(
      Graph(name)(
        layers ++ synapses :_*
      )
    )
  }
}