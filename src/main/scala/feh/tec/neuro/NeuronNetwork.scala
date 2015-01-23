package feh.tec.neuro

trait NeuronNetwork {
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

object NeuronNetwork{
  trait FixedNetworkStructure {
    self: NeuronNetwork =>

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

  trait MutableNNetwork extends NeuronNetwork{
    val neurons: Neurons[MutableNeuron]

    def fixed: ImmutableNNetwork
  }

  trait ImmutableNNetwork extends NeuronNetwork{
    val neurons: Neurons[ImmutableNeuron]
  }

}