package feh.tec.neuro.dsl

import feh.tec.neuro.NetworkDefinition
import NetworkDefinition._
import feh.util._

import scala.collection.mutable

object Neuro {
  def define(nn: Network): NetworkDefinition = {
    val neurons = nn.Layer.layers.flatMap{
      case (layer, neu) => neu.map(NeuronSelect(layer, _)).sortBy(_.asPair)
    }
    val synapses = nn.Layer.connectionsToBuild.toList flatMap {
      case (layer, buildSynapse) => buildSynapse()(layer).toList
    }

    new NetworkDefinition(nn.inputs, nn.outputs, neurons.toList, Synapses(synapses))
  }
}

trait Network{
  def inputs: List[NeuronSelect]
  def outputs: List[NeuronSelect]


  object Layer{
    def apply(layer: LayerIdent, neurons: NeuronIdent)(connections: => (LayerIdent => Synapses)): Unit = {
      def neuronsList = (1 to neurons).toList
      layers <<=(layer, _.map(_ ++ neuronsList).getOrElse(neuronsList))
      connectionsToBuild += layer -> (() => connections)
    }

    protected[dsl] val connectionsToBuild = mutable.Buffer[(LayerIdent, () => (LayerIdent => Synapses))]()
    protected[dsl] val layers: mutable.Map[LayerIdent, List[NeuronIdent]] = mutable.HashMap()
  }

  def connect(c: (LayerIdent => AbstractSynapse)*): LayerIdent => Synapses =
    layer => Synapses(c.flatMap(_(layer).toList).toList)

  object NoConnections extends (LayerIdent => Synapses){
    def apply(v1: LayerIdent) = Synapses(Nil)
  }

  case class AllOfLayer protected[dsl](l: LayerIdent)
  def allOfLayer(layer: LayerIdent) = AllOfLayer(layer)

  implicit class ConnectNeuron(neuron: NeuronIdent){
    def to(nn: NeuronSelect): LayerIdent => Synapse =
      layer => Synapse(NeuronSelect(layer, neuron), nn)
    def to(nn: List[NeuronSelect]): LayerIdent => Synapses =
      layer => Synapses(nn.map(Synapse(NeuronSelect(layer, neuron), _)))
    def to(all: AllOfLayer): LayerIdent => Synapses = to(Layer.layers(all.l).map(NeuronSelect(all.l, _)))
  }

  implicit class AbstractNeuronConnectionBuildWrapper(c: LayerIdent => AbstractSynapse){
    def withDelay(delay: NotNegative[Int]): LayerIdent => AbstractSynapse =
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