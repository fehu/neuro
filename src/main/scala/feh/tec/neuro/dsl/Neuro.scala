package feh.tec.neuro.dsl

import feh.tec.neuro.{NotNegative, NeuronNetwork}

object Neuro {
  def define[NN <: Network](nn: NN): NN = ???
}

trait Network{
  type TheNetwork <: NeuronNetwork

  def inputs: List[NeuronSelect]
  def outputs: List[NeuronSelect]


  type LayerIdent   = Int
  type NeuronIdent  = Int

  object Layer{
    def apply(layer: LayerIdent, neurons: NeuronIdent)(connections: (LayerIdent => NeuronConnections)*) = ???
  }

  def connect(c: (LayerIdent => AbstractNeuronConnection)*): LayerIdent => NeuronConnections = ??? //layer => new ConnectNeuron(neuron)

  case class AllOfLayer protected[dsl](l: LayerIdent)
  def allOfLayer(layer: LayerIdent) = AllOfLayer(layer)

  implicit class ConnectNeuron(neuron: NeuronIdent){
    def to(nn: NeuronSelect): LayerIdent => NeuronConnection =
      layer => NeuronConnection(NeuronSelect(layer, neuron), nn)
    def to(nn: List[NeuronSelect]): LayerIdent => NeuronConnections =
      layer => NeuronConnections(nn.map(NeuronConnection(NeuronSelect(layer, neuron), _)))
    def to(all: AllOfLayer): LayerIdent => NeuronConnection = ???
  }

  implicit class AbstractNeuronConnectionBuildWrapper(c: LayerIdent => AbstractNeuronConnection){
    def withDelay(delay: NotNegative[Int]): LayerIdent => AbstractNeuronConnection = ???
  }

/*
  abstract class Layer(val layer: Int,
                       val neurons: Int = neuronsNotDefined)
  {

  }
*/

  case class NeuronSelect protected[dsl](layer: LayerIdent, neuron: NeuronIdent)

  sealed trait AbstractNeuronConnection
  sealed trait SingleNeuronConnection extends AbstractNeuronConnection{
    def from: NeuronSelect
    def to: NeuronSelect
  }

  case class NeuronConnection protected[dsl](from: NeuronSelect, to: NeuronSelect) extends SingleNeuronConnection
  case class DelayedNeuronConnection protected[dsl](from: NeuronSelect, to: NeuronSelect, delay: NotNegative[Int]) extends SingleNeuronConnection

  case class NeuronConnections protected[dsl](connections: List[SingleNeuronConnection]) extends AbstractNeuronConnection
  
  implicit class CreateNeuronIdent(layer: LayerIdent){
    def ~(neurons: NeuronIdent*) = neurons.toList map (new NeuronSelect(layer, _))
    def ~(neurons: Range) = neurons.toList map (new NeuronSelect(layer, _))
  }

  implicit class NeuronIdentListOps(nList: List[NeuronSelect]){
    def and(nList2: List[NeuronSelect]) = nList ::: nList2
  }
}