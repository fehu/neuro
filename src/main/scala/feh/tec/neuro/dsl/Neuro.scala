package feh.tec.neuro.dsl

import feh.tec.neuro.{NetworkDefinition, NotNegative, NeuronNetwork}
import NetworkDefinition._
import feh.util.ScopedState

object Neuro {
  def define(nn: Network): NetworkDefinition = ???
}

trait Network{
  type TheNetwork <: NeuronNetwork

  def inputs: List[NeuronSelect]
  def outputs: List[NeuronSelect]


  object Layer{
    def apply(layer: LayerIdent, neurons: NeuronIdent)(connections: => (LayerIdent => NeuronConnections)) = ???
//      currentLayer.doWith(Some(layer)){
//
//      }

//    protected[dsl] val currentLayer = new ScopedState[Option[LayerIdent]](None)
  }

  def connect(c: (LayerIdent => AbstractNeuronConnection)*): LayerIdent => NeuronConnections =
    layer => NeuronConnections(c.flatMap(_(layer).toList).toList)

  object NoConnections extends (LayerIdent => NeuronConnections){
    def apply(v1: LayerIdent) = NeuronConnections(Nil)
  }

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
    def withDelay(delay: NotNegative[Int]): LayerIdent => AbstractNeuronConnection =
      layer => c(layer).transform(_.copy(delay = delay))
  }
  
  implicit class CreateNeuronIdent(layer: LayerIdent){
    def ~(neurons: NeuronIdent*) = neurons.toList map (new NeuronSelect(layer, _))
    def ~(neurons: Range) = neurons.toList map (new NeuronSelect(layer, _))
  }

  implicit class NeuronIdentListOps(nList: List[NeuronSelect]){
    def and(nList2: List[NeuronSelect]) = nList ::: nList2
  }
}