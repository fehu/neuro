package feh.tec.neuro.definition

trait ValidNetworkDefinition extends NetworkDefinition{
  import feh.tec.neuro.definition.NetworkDefinition._

  trait StructureProvider{
    def inputs(of: NeuronSelect): List[NetworkPoint]
    def output(of: NeuronSelect): NetworkPoint
  }

  val get: StructureProvider
}
