package feh.tec.neuro.definition

import feh.tec.neuro.definition.NetworkDefinition._
import feh.util.NotNegative

object NetworkDefinition{
  type LayerIdent   = Int
  type NeuronIdent  = Int
  
  case class NeuronSelect protected[neuro](layer: LayerIdent, neuron: NeuronIdent) {
    override def toString = s"$layer:$neuron"
    def asPair = layer -> neuron
  }
  
  sealed trait AbstractSynapse{
    def transform(f: Synapse => Synapse): AbstractSynapse
    def toList: List[Synapse]
  }

  case class Synapse protected[neuro](from: NeuronSelect, to: NeuronSelect, delay: NotNegative[Int] = 0) extends AbstractSynapse{
    def transform(f: Synapse => Synapse): Synapse = f(this)
    def toList = this :: Nil

    override def toString = s"$from-${if(delay.value == 0) "" else s"[${delay.value}]"}->$to"
  }
  case class Synapses protected[neuro](connections: List[Synapse]) extends AbstractSynapse{
    def transform(f: Synapse => Synapse): Synapses = copy(connections.map(f))
    def toList = connections

    lazy val listing = connections.mkString(", ")
    override def toString = s"Synapses($listing)"
  }
}

/** Neural network descriptive definition */
class NetworkDefinition(val inputs: List[NeuronSelect],
                        val outputs: List[NeuronSelect],
                        val neurons: List[NeuronSelect],
                        val synapses: Synapses){
  def this(nd: NetworkDefinition) = this(nd.inputs, nd.outputs, nd.neurons, nd.synapses)

  override def toString = s"NetworkDefinition: \n\t$sInputs\n\t$sOutputs\n\t$sNeurons\n\t$sSynapses"

  private def sInputs    = s"inputs: ${inputs.mkString(", ")}"
  private def sOutputs   = s"outputs: ${outputs.mkString(", ")}"
  private def sNeurons   = s"neurons: ${neurons.mkString(", ")}"
  private def sSynapses  = s"synapses: ${synapses.listing}"
}
