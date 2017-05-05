# What is Cartridge?

A Cartridge is a set of resources that are loaded into the Platform for a particular project. They may contain anything from a simple reference implementation for a technology to a set of best practice examples for building, deploying, and managing a technology stack that can be used by a project.

This cartridge consists of source code repositories and Jenkins jobs.

## Source code repositories

Cartridge loads the source code repositories:

* [Puppet Control Repo](https://github.com/mrzarquon/adop-cartridge-puppet-control-repo.git)

## Jenkins Jobs

This cartridge provides a Jenkins Pipeline view to -

* Detect repo changes
* Perform Verify checks using Puppet Verify
* Perform Lint checks using Puppet Lint
* Run noop codeDeploy
* Run codeDepoloy

This cartidge is designed to be used alongside the [ADOP Puppet platform extension](https://github.com/aliciasteen/adop-platform-extension-puppet).

# License
Please view [license information](LICENSE.md) for the software contained on this image.

## Documentation
Documentation will be captured within this README.md and this repository's Wiki.

## Issues
If you have any problems with or questions about this image, please contact us through a [GitHub issue](https://github.com/aliciasteen/adop-cartridge-puppet/issues).

## Contribute
You are invited to contribute new features, fixes, or updates, large or small; we are always thrilled to receive pull requests, and do our best to process them as fast as we can.

Before you start to code, we recommend discussing your plans through a [GitHub issue](https://github.com/aliciasteen/adop-cartridge-puppet/issues), especially for more ambitious contributions. This gives other contributors a chance to point you in the right direction, give you feedback on your design, and help you find out if someone else is working on the same thing.