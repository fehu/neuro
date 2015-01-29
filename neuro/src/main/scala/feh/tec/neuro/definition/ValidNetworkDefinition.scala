package feh.tec.neuro.definition

trait ValidNetworkDefinition extends NetworkDefinition{
  import feh.tec.neuro.definition.NetworkDefinition._

  trait StructureProvider{
    def inputs(of: NeuronSelect): List[NeuronSelect]
    def outputs(of: NeuronSelect): List[NeuronSelect]
  }

  val get: StructureProvider

  override def toString = "Valid" + super.toString
}
