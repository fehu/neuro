package feh.tec.neuro

trait NeuralNetwork {
  /** Number Network of inputs */
  def inputs: Int

  /** Number Network of outputs */
  def outputs: Int

  trait Neurons[+Neu <: Neuron]{
    def input: Seq[Neu]
    def output: Seq[Neu]

    def all: Seq[Neu]
  }

  val neurons: Neurons[Neuron]

  def output(input: Seq[Float]): Seq[Float]
}

object NeuralNetwork{
  trait FixedNetworkStructure {
    self: NeuralNetwork =>

    val inputs: Int
    val outputs: Int

    trait FixedNeuronStructure[+Neu <: Neuron] extends Neurons[Neu]{
      val input: Seq[Neu]
      val output: Seq[Neu]

      val all: Seq[Neu]
    }

    val neurons: FixedNeuronStructure[Neuron]

    assert(neurons.input.size == inputs,    "wrong number of input neurons")
    assert(neurons.output.size == outputs,  "wrong number of output neurons")
  }

  trait MutableNNetwork extends NeuralNetwork{
    val neurons: Neurons[MutableNeuron]

    def fixed: ImmutableNNetwork
  }

  trait ImmutableNNetwork extends NeuralNetwork{
    val neurons: Neurons[ImmutableNeuron]
  }

}