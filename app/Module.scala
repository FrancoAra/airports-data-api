
import com.google.inject.AbstractModule
import services.{ BatchImporter, MongoBatchImporter }

class Module extends AbstractModule {
  def configure() = {

    bind(classOf[BatchImporter])
      .to(classOf[MongoBatchImporter]).asEagerSingleton
  }
}
