package feh.tec.test.neuro

import feh.tec.neuro.definition.NetworkDefinitionValidation
import feh.tec.neuro.dsl._

/** Neural Network DSL Test
 *  inputs: 2
 *  outputs: 2
 *  hidden: 1 layer with 5 neurons
 */

object NN1 extends App{
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

  val valid = NetworkDefinitionValidation(nNetwork)

  println(valid)
}

/** Invalid Neural Network Test
  *
  */
object NN2 extends App{
  val nNetwork = Neuro define new Network{
    Layer(0, neurons = 4)(
      connect(
        1 to 1~(1 to 3),
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

    Layer(2, neurons = 1)(
      connect(
        1 to 0~2 withDelay 2
      )
    )

    Layer(3, neurons = 0)(NoConnections)

    def inputs = 0~3 and 0~4
    def outputs = 2~(1, 2)

  }

  val valid = NetworkDefinitionValidation(nNetwork)

  println(valid)
}